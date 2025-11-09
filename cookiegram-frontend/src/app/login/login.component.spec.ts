import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the login button', () => {
    const button = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(button).toBeTruthy();
  });

  it('should call onForgotPassword without errors', () => {
  spyOn(window, 'alert');
  component.onForgotPassword();
  expect(window.alert).toHaveBeenCalledWith('Password reset feature coming soon!');
});

});
