import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { GoalService } from '../service/goal.service';
import { IGoal } from '../goal.model';
import { GoalFormService } from './goal-form.service';

import { GoalUpdateComponent } from './goal-update.component';

describe('Goal Management Update Component', () => {
  let comp: GoalUpdateComponent;
  let fixture: ComponentFixture<GoalUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let goalFormService: GoalFormService;
  let goalService: GoalService;
  let familyService: FamilyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GoalUpdateComponent],
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
      .overrideTemplate(GoalUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GoalUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    goalFormService = TestBed.inject(GoalFormService);
    goalService = TestBed.inject(GoalService);
    familyService = TestBed.inject(FamilyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Family query and add missing value', () => {
      const goal: IGoal = { id: 6150 };
      const family: IFamily = { id: 15147 };
      goal.family = family;

      const familyCollection: IFamily[] = [{ id: 15147 }];
      jest.spyOn(familyService, 'query').mockReturnValue(of(new HttpResponse({ body: familyCollection })));
      const additionalFamilies = [family];
      const expectedCollection: IFamily[] = [...additionalFamilies, ...familyCollection];
      jest.spyOn(familyService, 'addFamilyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ goal });
      comp.ngOnInit();

      expect(familyService.query).toHaveBeenCalled();
      expect(familyService.addFamilyToCollectionIfMissing).toHaveBeenCalledWith(
        familyCollection,
        ...additionalFamilies.map(expect.objectContaining),
      );
      expect(comp.familiesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const goal: IGoal = { id: 6150 };
      const family: IFamily = { id: 15147 };
      goal.family = family;

      activatedRoute.data = of({ goal });
      comp.ngOnInit();

      expect(comp.familiesSharedCollection).toContainEqual(family);
      expect(comp.goal).toEqual(goal);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGoal>>();
      const goal = { id: 2775 };
      jest.spyOn(goalFormService, 'getGoal').mockReturnValue(goal);
      jest.spyOn(goalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ goal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: goal }));
      saveSubject.complete();

      // THEN
      expect(goalFormService.getGoal).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(goalService.update).toHaveBeenCalledWith(expect.objectContaining(goal));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGoal>>();
      const goal = { id: 2775 };
      jest.spyOn(goalFormService, 'getGoal').mockReturnValue({ id: null });
      jest.spyOn(goalService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ goal: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: goal }));
      saveSubject.complete();

      // THEN
      expect(goalFormService.getGoal).toHaveBeenCalled();
      expect(goalService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGoal>>();
      const goal = { id: 2775 };
      jest.spyOn(goalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ goal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(goalService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFamily', () => {
      it('should forward to familyService', () => {
        const entity = { id: 15147 };
        const entity2 = { id: 25490 };
        jest.spyOn(familyService, 'compareFamily');
        comp.compareFamily(entity, entity2);
        expect(familyService.compareFamily).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
