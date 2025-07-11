import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FamilyDetailComponent } from './family-detail.component';

describe('Family Management Detail Component', () => {
  let comp: FamilyDetailComponent;
  let fixture: ComponentFixture<FamilyDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FamilyDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./family-detail.component').then(m => m.FamilyDetailComponent),
              resolve: { family: () => of({ id: 15147 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FamilyDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FamilyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load family on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FamilyDetailComponent);

      // THEN
      expect(instance.family()).toEqual(expect.objectContaining({ id: 15147 }));
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
