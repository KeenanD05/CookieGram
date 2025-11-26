import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private loggedIn = false;

  setLoggedIn(value: boolean) {
    this.loggedIn = value;
  }

  isLoggedIn() {
    return this.loggedIn;
  }
}
