import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  onForgotPassword() {
    alert('Password reset feature coming soon!');
  }

  submit() {
    if (!this.username || !this.password) {
      this.error = 'Please enter both username and password.';
      return;
    }

    console.log('Attempting login with:', this.username);

    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        console.log('Login response:', res);

        const userRole = res.roles?.[0]; // Get the user's role
        localStorage.setItem('cg_role', userRole); // Save to localStorage
        console.log('Detected user role:', userRole);

        switch (userRole) {
          case 'ROLE_USER':
            this.router.navigate(['/customer']);
            break;
          case 'ROLE_EMPLOYEE':
          case 'ROLE_STAFF': // ✅ Added this case
            this.router.navigate(['/employee']);
            break;
          case 'ROLE_ADMIN':
            this.router.navigate(['/admin']);
            break;
          default:
            console.warn('⚠️ No matching route for role:', userRole);
            this.error = 'Unknown role or access not granted.';
            this.router.navigate(['/']);
            break;
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        this.error = 'Invalid username or password.';
      }
    });
  }
}
