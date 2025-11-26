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
  name = '';
  email = '';
  address = '';
  deliveryDate = '';
  minDate = '';
  shipping = 4.99;

  stripe!: Stripe;
  card!: StripeCardElement;
  cardError = '';
  formError = '';
  dateError = '';
  isPaying = false;

  constructor(
    private cartService: CartService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.items = this.cartService.getItems();
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minDate = tomorrow.toISOString().split('T')[0];
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
    this.card = elements.create("card");
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

  async checkout() {
    this.cardError = '';
    this.formError = '';
    this.dateError = '';

    // Validate required fields
    if (!this.name || !this.email || !this.address || !this.deliveryDate) {
      this.formError = 'All fields are required.';
      return;
    }

    // Validate delivery date
    const selectedDate = new Date(this.deliveryDate);
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    if (selectedDate < tomorrow) {
      this.dateError = 'Delivery date must be at least tomorrow.';
      return;
    }

    if (!this.stripe || !this.card) {
      this.cardError = "Stripe not initialized";
      return;
    }

    this.isPaying = true;  // Prevent double submission

    try {
      // 1. Create PaymentMethod
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
        this.isPaying = false;
        return;
      }

      // 2. Create payment intent
      const response = await this.http.post<any>(
        "http://localhost:8080/payments/create-payment-intent",
        {
          amount: this.total,
          description: "CookieGram Order",
          customerEmail: this.email,
          paymentMethodId: pm.paymentMethod?.id
        }
      ).toPromise();

      if (!response.clientSecret) {
        this.cardError = "No client secret from backend";
        this.isPaying = false;
        return;
      }

      // 3. Confirm card payment
      const confirmation = await this.stripe.confirmCardPayment(response.clientSecret);

      if (confirmation.error) {
        this.cardError = confirmation.error.message ?? "Payment failed";
        this.isPaying = false;
        return;
      }

      if (confirmation.paymentIntent?.status === "succeeded") {
        // 4. Create the order
        const orderRequest = {
          customerEmail: this.email,
          customerName: this.name,
          shippingAddress: this.address,
          requiredShippingDate: new Date(this.deliveryDate + 'T00:00:00').toISOString(),
          items: this.items.map(item => ({
            cookieId: item.productId,  // Ensure this maps correctly
            quantity: item.quantity
          }))
        };

        this.http.post('http://localhost:8080/api/orders', orderRequest).subscribe({
          next: (orderResponse: any) => {
            console.log('Order created:', orderResponse);

            // 5. Send confirmation email (only once, after order success)
            this.http.post('http://localhost:8080/payments/confirm-payment', {
              paymentIntentId: confirmation.paymentIntent.id,
              paymentMethodId: pm.paymentMethod?.id,
              customerEmail: this.email,
              customerName: this.name
            }).subscribe({
              next: () => console.log('Email sent!'),
              error: err => console.error('Failed to send email:', err)
            });

            this.cartService.clearCart();
            this.router.navigate(['/thank-you']);
          },
          error: (err) => {
            if (err.status === 409) {
              const nextDate = err.error.nextAvailableDate;
              this.formError = `Selected date is unavailable. Next available: ${nextDate}`;
            } else {
              this.formError = 'Failed to create order. Please try again.';
            }
            console.error('Order creation failed:', err);
            this.isPaying = false;
          }
        });
      }
    } catch (error) {
      this.formError = 'An error occurred during checkout.';
      console.error(error);
      this.isPaying = false;
    }
  }
}