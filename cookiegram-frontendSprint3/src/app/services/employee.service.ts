import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Employee } from '../models/employee';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  // Change this to match your backend teammate's API endpoint
  private api = 'http://localhost:8080/api/admin/register-staff';

  constructor(private http: HttpClient) {}

  // POST: create new employee
createEmployee(emp: Employee) {
  return this.http.post<Employee>(
    this.api,
    emp,
    { withCredentials: true }   // ⭐ REQUIRED for Spring Security
  );
}



  // (For future sprint) GET: list all employees
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.api);
  }

  // (For future sprint) DELETE: remove employee
  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  // (For future sprint) PUT or PATCH: update employee
  updateEmployee(id: number, updated: Partial<Employee>): Observable<Employee> {
    return this.http.put<Employee>(`${this.api}/${id}`, updated);
  }
}
