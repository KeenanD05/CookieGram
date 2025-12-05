import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {

  username = '';
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  mobileNumber = '';

  successMessage = '';
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  submit() {
    this.errorMessage = '';
    this.successMessage = '';

    // FRONTEND VALIDATION
    if (!this.username || !this.firstName || !this.lastName || !this.email || !this.password || !this.mobileNumber) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    const userData = {
      username: this.username,
      email: this.email,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName,
      mobileNumber: this.mobileNumber
    };

    this.http.post('http://localhost:8080/api/auth/signup', userData).subscribe({
      next: () => {
        this.successMessage = 'Account created successfully!';
        setTimeout(() => this.router.navigate(['/login']), 1000);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Signup failed. Please check your details.';
      }
    });
  }
}

