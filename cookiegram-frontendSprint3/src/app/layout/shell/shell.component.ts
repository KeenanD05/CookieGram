import { Component } from '@angular/core'; 
import { Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../header/header.component';
import { FooterComponent } from '../../footer/footer.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']
})
export class ShellComponent {

  constructor(private router: Router) {}

  get isAdminRoute(): boolean {
    return this.router.url.startsWith('/admin');
  }

  get isStaffRoute(): boolean {
    return this.router.url.startsWith('/employee');
  }
}
