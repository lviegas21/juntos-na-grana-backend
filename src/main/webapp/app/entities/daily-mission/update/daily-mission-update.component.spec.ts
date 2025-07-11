import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { DailyMissionService } from '../service/daily-mission.service';
import { IDailyMission } from '../daily-mission.model';
import { DailyMissionFormService } from './daily-mission-form.service';

import { DailyMissionUpdateComponent } from './daily-mission-update.component';

describe('DailyMission Management Update Component', () => {
  let comp: DailyMissionUpdateComponent;
  let fixture: ComponentFixture<DailyMissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dailyMissionFormService: DailyMissionFormService;
  let dailyMissionService: DailyMissionService;
  let familyService: FamilyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DailyMissionUpdateComponent],
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
      .overrideTemplate(DailyMissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DailyMissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dailyMissionFormService = TestBed.inject(DailyMissionFormService);
    dailyMissionService = TestBed.inject(DailyMissionService);
    familyService = TestBed.inject(FamilyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Family query and add missing value', () => {
      const dailyMission: IDailyMission = { id: 31998 };
      const family: IFamily = { id: 15147 };
      dailyMission.family = family;

      const familyCollection: IFamily[] = [{ id: 15147 }];
      jest.spyOn(familyService, 'query').mockReturnValue(of(new HttpResponse({ body: familyCollection })));
      const additionalFamilies = [family];
      const expectedCollection: IFamily[] = [...additionalFamilies, ...familyCollection];
      jest.spyOn(familyService, 'addFamilyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dailyMission });
      comp.ngOnInit();

      expect(familyService.query).toHaveBeenCalled();
      expect(familyService.addFamilyToCollectionIfMissing).toHaveBeenCalledWith(
        familyCollection,
        ...additionalFamilies.map(expect.objectContaining),
      );
      expect(comp.familiesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const dailyMission: IDailyMission = { id: 31998 };
      const family: IFamily = { id: 15147 };
      dailyMission.family = family;

      activatedRoute.data = of({ dailyMission });
      comp.ngOnInit();

      expect(comp.familiesSharedCollection).toContainEqual(family);
      expect(comp.dailyMission).toEqual(dailyMission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMission>>();
      const dailyMission = { id: 27924 };
      jest.spyOn(dailyMissionFormService, 'getDailyMission').mockReturnValue(dailyMission);
      jest.spyOn(dailyMissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyMission }));
      saveSubject.complete();

      // THEN
      expect(dailyMissionFormService.getDailyMission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dailyMissionService.update).toHaveBeenCalledWith(expect.objectContaining(dailyMission));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMission>>();
      const dailyMission = { id: 27924 };
      jest.spyOn(dailyMissionFormService, 'getDailyMission').mockReturnValue({ id: null });
      jest.spyOn(dailyMissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyMission }));
      saveSubject.complete();

      // THEN
      expect(dailyMissionFormService.getDailyMission).toHaveBeenCalled();
      expect(dailyMissionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyMission>>();
      const dailyMission = { id: 27924 };
      jest.spyOn(dailyMissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyMission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dailyMissionService.update).toHaveBeenCalled();
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
