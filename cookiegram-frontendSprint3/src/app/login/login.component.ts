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

      
      // Debug
      

      // ⭐ FIX: Check ALL roles, not just the first one ⭐
      localStorage.setItem('cg_roles', JSON.stringify(res.roles));


localStorage.setItem('cg_roles', JSON.stringify(res.roles));

const roleList = res.roles;
console.log('Detected roles:', roleList);
// Correct priority: ADMIN → EMPLOYEE → CUSTOMER
if (roleList.includes('ROLE_ADMIN')) {
  this.router.navigate(['/admin']);
} 
else if (roleList.includes('ROLE_EMPLOYEE') || roleList.includes('ROLE_STAFF')) {
  this.router.navigate(['/employee']);
} 
else if (roleList.includes('ROLE_USER')) {
  this.router.navigate(['/customer']);
} 
else {
  this.error = 'Unknown role or access not granted.';
}


    },
    error: (err) => {
      console.error('Login error:', err);
      this.error = 'Invalid username or password.';
    }
  });
}

}
