import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { Router } from '@angular/router';
import { Employee } from '../../models/employee';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-employee.component.html',
  styleUrls: ['./add-employee.component.css']
})
export class AddEmployeeComponent {
  errorMessage: string = '';
  successMessage: string = '';   

  employee: Employee = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    username: '',
    mobileNumber: ''
  };

  constructor(
    private employeeService: EmployeeService,
    private router: Router
  ) {}

  submit() {
  this.errorMessage = '';
  this.successMessage = '';
 // reset

    this.employeeService.createEmployee(this.employee).subscribe({
      next: () => {
        this.successMessage = 'Employee created successfully!';
        
     
      },
    error: (err) => {
      console.error('Employee creation error:', err);

      // ✔ Case 1: Backend sends a message string
      if (err.error && typeof err.error === 'string') {
        this.errorMessage = err.error;
      }
      // ✔ Case 2: Backend sends { message: "..."}
      else if (err.error?.message) {
        this.errorMessage = err.error.message;
      }
      // ✔ Case 3: Validation errors array
      else if (Array.isArray(err.error?.errors)) {
        this.errorMessage = err.error.errors
          .map((e: any) => `${e.field}: ${e.defaultMessage}`)
          .join('<br>');
      }
      // ✔ Fallback
      else {
        this.errorMessage = 'An unexpected error occurred.';
      }
    }
  });
}

}
