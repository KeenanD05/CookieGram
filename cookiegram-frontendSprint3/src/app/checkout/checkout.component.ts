import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService, CartItem } from '../services/cart.service';
import { HttpClient } from '@angular/common/http';
import { loadStripe, Stripe, StripeCardElement } from '@stripe/stripe-js';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit, AfterViewInit {

  items: CartItem[] = [];

  // Customer Information
  name = '';
  email = '';

  // Recipient info
  recipientName = '';
  recipientEmail = '';
  recipientPhone = '';
  specialInstructions = '';

  // Delivery date
  deliveryDate: string = '';
  minDeliveryDate: string = '';

  // Address fields
  street = '';
  city = '';
  province = '';
  postalCode = '';

  shipping = 4.99;

  stripe!: Stripe;
  card!: StripeCardElement;
  cardError = '';
  formError = '';
  submitted = false;

  constructor(
    private cartService: CartService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.items = this.cartService.getItems();
    this.setMinDeliveryDate();
  }

  setMinDeliveryDate() {
    const today = new Date();
    today.setDate(today.getDate() + 2); // 2 days ahead
    this.minDeliveryDate = today.toISOString().split('T')[0];
  }

  isFormValid(): boolean {
    if (!this.name.trim()) return false;
    if (!this.email.trim()) return false;
    if (!this.recipientEmail.trim()) return false;
    if (!this.recipientName.trim()) return false;
    if (!this.recipientPhone.trim()) return false;

    if (!this.items || this.items.length === 0) return false;

    if (!this.deliveryDate) return false;
    if (new Date(this.deliveryDate) < new Date(this.minDeliveryDate)) return false;

    if (!this.street.trim()) return false;
    if (!this.city.trim()) return false;
    if (!this.province.trim()) return false;
    if (!this.postalCode.trim()) return false;

    return true;
  }


  async ngAfterViewInit() {
    const stripeLoaded = await loadStripe(
      "pk_test_51SSq9ePRxl5rwImwJ4ZpZSTevjG9ldAbrwN8Q8fRd0y3tHYs2tRtMZmQrAN1IGHjZu3ejX51Wm2UPqLOuKuPvnRa00acNWyg0M"
    );

    if (!stripeLoaded) {
      this.formError = "Stripe failed to load.";
      return;
    }

    this.stripe = stripeLoaded;

    const elements = this.stripe.elements();
    this.card = elements.create("card", { hidePostalCode: true });
    this.card.mount("#card-element");

    this.card.on("change", event => {
      this.cardError = event.error?.message ?? "";
    });
  }

  get subtotal() {
    return this.items.reduce((sum, it) => sum + it.price * it.quantity, 0);
  }

  get tax() {
    return this.subtotal * 0.13;
  }

  get total() {
    return this.subtotal + this.shipping + this.tax;
  }

  back() {
    this.router.navigate(['/products']);
  }

  // -----------------------------------------------------
  //                      CHECKOUT
  // -----------------------------------------------------
  async checkout() {
     this.submitted = true;
    this.cardError = "";
    this.formError = "";
   if (!this.isFormValid()) {
    this.formError = "Please fill out all required fields before proceeding.";
    return;
  }

    if (!this.stripe || !this.card) {
      this.cardError = "Stripe not initialized.";
      return;
    }

    // 1. Create Payment Method
    const pm = await this.stripe.createPaymentMethod({
      type: "card",
      card: this.card,
      billing_details: {
        name: this.name,
        email: this.email
      }
    });

    if (pm.error) {
      this.cardError = pm.error.message ?? "Card error";
      return;
    }
   
  this.formError = ''

    // 2. Create Payment Intent
    this.http.post<any>(
      "http://localhost:8080/payments/create-payment-intent",
      {
        amount: this.total,
        description: "CookieGram Order",
        customerEmail: this.email,
        paymentMethodId: pm.paymentMethod?.id
      }
    ).subscribe(async response => {

      if (!response.clientSecret) {
        this.cardError = "Payment failed: missing client secret.";
        return;
      }

      // 3. Confirm payment
      const confirmation = await this.stripe!.confirmCardPayment(response.clientSecret);

      if (confirmation.error) {
        this.cardError = confirmation.error.message ?? "Payment failed";
        return;
      }

      if (confirmation.paymentIntent?.status === "succeeded") {

        // 4. Confirm payment on server (optional)
        this.http.post(
          "http://localhost:8080/payments/confirm-payment",
          {
            paymentIntentId: confirmation.paymentIntent.id,
            paymentMethodId: pm.paymentMethod?.id,
            customerEmail: this.email,
            customerName: this.name,
            recipientEmail: this.recipientEmail
          }
        ).subscribe();

        // ---------------------------------------------
        // 5. CREATE ORDER IN DATABASE
        // ---------------------------------------------
        const orderPayload = {
          customerName: this.name,
          customerEmail: this.email,

          shippingAddress: `${this.street}, ${this.city}, ${this.province} ${this.postalCode}`,

          requiredShippingDate: this.deliveryDate + "T00:00:00",
          totalAmount: this.total,

          receiver: {
            name: this.recipientName,
            email: this.recipientEmail,
            phoneNumber: this.recipientPhone,
            specialInstructions: this.specialInstructions
          },

         items: this.items.map(it => ({
  cookieId: it.productId,
  quantity: it.quantity,
  icingFlavor: it.icing.toUpperCase(),   // match enum
  message: it.message
}))

        };

        this.http.post("http://localhost:8080/api/orders", orderPayload)
          .subscribe({
            next: () => {
              this.cartService.clearCart();
              this.router.navigate(['/thank-you']);
            },
            error: err => {
              console.error("ORDER CREATION FAILED:", err);
              this.formError = "Payment succeeded but order creation failed. Your card was charged.";
            }
          });
      }
    });
  }

}
