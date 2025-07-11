import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { WalletDetailComponent } from './wallet-detail.component';

describe('Wallet Management Detail Component', () => {
  let comp: WalletDetailComponent;
  let fixture: ComponentFixture<WalletDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./wallet-detail.component').then(m => m.WalletDetailComponent),
              resolve: { wallet: () => of({ id: 15861 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(WalletDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WalletDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load wallet on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WalletDetailComponent);

      // THEN
      expect(instance.wallet()).toEqual(expect.objectContaining({ id: 15861 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
