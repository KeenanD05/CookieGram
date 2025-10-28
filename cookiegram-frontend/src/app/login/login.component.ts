import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  onForgotPassword() {
  alert('Password reset feature coming soon!');
}


  constructor(private auth: AuthService, private router: Router) {}

  submit() {
    if (!this.username || !this.password) {
      this.error = 'Please enter both username and password.';
      return;
    }

    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        if (res.role === 'CUSTOMER') this.router.navigate(['/customer']);
        else if (res.role === 'EMPLOYEE') this.router.navigate(['/employee']);
        else if (res.role === 'ADMIN') this.router.navigate(['/admin']);
        else this.error = 'Unknown role.';
      },
      error: () => {
        this.error = 'Invalid username or password.';
      }
    });
  }
}
