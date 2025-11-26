import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
  <footer class="cg-footer">
    <div class="row">
      <div class="col">
        <div class="brand">🍪 CookieGram</div>
        <p>Delivering Joy. One Cookie at a time...</p>
      </div>
      <div class="col">
        <div class="muted">© {{year}} CookieGram • Made with love & butter</div>
      </div>
    </div>
  </footer>
  `,
  styles: [`
    .cg-footer {
  text-align: center;
  padding: 1rem;
  background: linear-gradient(to right, #ffe2ca, #fff6ef);
  border-top: 2px solid #f2d4b4;
  color: var(--text-accent);
  font-weight: 500;
}

  `]
})
export class FooterComponent {
  year = new Date().getFullYear();
}
