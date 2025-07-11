import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { FamilyService } from '../service/family.service';
import { IFamily } from '../family.model';
import { FamilyFormService } from './family-form.service';

import { FamilyUpdateComponent } from './family-update.component';

describe('Family Management Update Component', () => {
  let comp: FamilyUpdateComponent;
  let fixture: ComponentFixture<FamilyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let familyFormService: FamilyFormService;
  let familyService: FamilyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FamilyUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FamilyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FamilyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    familyFormService = TestBed.inject(FamilyFormService);
    familyService = TestBed.inject(FamilyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const family: IFamily = { id: 25490 };

      activatedRoute.data = of({ family });
      comp.ngOnInit();

      expect(comp.family).toEqual(family);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFamily>>();
      const family = { id: 15147 };
      jest.spyOn(familyFormService, 'getFamily').mockReturnValue(family);
      jest.spyOn(familyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ family });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: family }));
      saveSubject.complete();

      // THEN
      expect(familyFormService.getFamily).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(familyService.update).toHaveBeenCalledWith(expect.objectContaining(family));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFamily>>();
      const family = { id: 15147 };
      jest.spyOn(familyFormService, 'getFamily').mockReturnValue({ id: null });
      jest.spyOn(familyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ family: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: family }));
      saveSubject.complete();

      // THEN
      expect(familyFormService.getFamily).toHaveBeenCalled();
      expect(familyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFamily>>();
      const family = { id: 15147 };
      jest.spyOn(familyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ family });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(familyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
