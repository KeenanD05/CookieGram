import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LandingComponent } from './landing.component';
import { PromotionsService } from '../services/promotions.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

describe('LandingComponent', () => {
  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;
  let mockPromoService: jasmine.SpyObj<PromotionsService>;

  beforeEach(async () => {
    // Create a mock PromotionsService with getPromotions() returning fake data
    mockPromoService = jasmine.createSpyObj('PromotionsService', ['getPromotions']);
    mockPromoService.getPromotions.and.returnValue(
      of([{ id: 1, title: 'Test Promo', description: 'Yum!' }])
    );

    await TestBed.configureTestingModule({
      imports: [LandingComponent, HttpClientTestingModule],
      providers: [{ provide: PromotionsService, useValue: mockPromoService }]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit()
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should display the main heading', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Fresh Bakes');
  });

  it('should load promotions automatically', () => {
    expect(component.promos.length).toBe(1);
    expect(component.promos[0].title).toBe('Test Promo');
  });
});
