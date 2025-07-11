import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDailyMission } from 'app/entities/daily-mission/daily-mission.model';
import { DailyMissionService } from 'app/entities/daily-mission/service/daily-mission.service';
import { MissionStatusRecordService } from '../service/mission-status-record.service';
import { IMissionStatusRecord } from '../mission-status-record.model';
import { MissionStatusRecordFormService } from './mission-status-record-form.service';

import { MissionStatusRecordUpdateComponent } from './mission-status-record-update.component';

describe('MissionStatusRecord Management Update Component', () => {
  let comp: MissionStatusRecordUpdateComponent;
  let fixture: ComponentFixture<MissionStatusRecordUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let missionStatusRecordFormService: MissionStatusRecordFormService;
  let missionStatusRecordService: MissionStatusRecordService;
  let dailyMissionService: DailyMissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MissionStatusRecordUpdateComponent],
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
      .overrideTemplate(MissionStatusRecordUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MissionStatusRecordUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    missionStatusRecordFormService = TestBed.inject(MissionStatusRecordFormService);
    missionStatusRecordService = TestBed.inject(MissionStatusRecordService);
    dailyMissionService = TestBed.inject(DailyMissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DailyMission query and add missing value', () => {
      const missionStatusRecord: IMissionStatusRecord = { id: 16586 };
      const mission: IDailyMission = { id: 27924 };
      missionStatusRecord.mission = mission;

      const dailyMissionCollection: IDailyMission[] = [{ id: 27924 }];
      jest.spyOn(dailyMissionService, 'query').mockReturnValue(of(new HttpResponse({ body: dailyMissionCollection })));
      const additionalDailyMissions = [mission];
      const expectedCollection: IDailyMission[] = [...additionalDailyMissions, ...dailyMissionCollection];
      jest.spyOn(dailyMissionService, 'addDailyMissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ missionStatusRecord });
      comp.ngOnInit();

      expect(dailyMissionService.query).toHaveBeenCalled();
      expect(dailyMissionService.addDailyMissionToCollectionIfMissing).toHaveBeenCalledWith(
        dailyMissionCollection,
        ...additionalDailyMissions.map(expect.objectContaining),
      );
      expect(comp.dailyMissionsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const missionStatusRecord: IMissionStatusRecord = { id: 16586 };
      const mission: IDailyMission = { id: 27924 };
      missionStatusRecord.mission = mission;

      activatedRoute.data = of({ missionStatusRecord });
      comp.ngOnInit();

      expect(comp.dailyMissionsSharedCollection).toContainEqual(mission);
      expect(comp.missionStatusRecord).toEqual(missionStatusRecord);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMissionStatusRecord>>();
      const missionStatusRecord = { id: 31132 };
      jest.spyOn(missionStatusRecordFormService, 'getMissionStatusRecord').mockReturnValue(missionStatusRecord);
      jest.spyOn(missionStatusRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ missionStatusRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: missionStatusRecord }));
      saveSubject.complete();

      // THEN
      expect(missionStatusRecordFormService.getMissionStatusRecord).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(missionStatusRecordService.update).toHaveBeenCalledWith(expect.objectContaining(missionStatusRecord));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMissionStatusRecord>>();
      const missionStatusRecord = { id: 31132 };
      jest.spyOn(missionStatusRecordFormService, 'getMissionStatusRecord').mockReturnValue({ id: null });
      jest.spyOn(missionStatusRecordService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ missionStatusRecord: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: missionStatusRecord }));
      saveSubject.complete();

      // THEN
      expect(missionStatusRecordFormService.getMissionStatusRecord).toHaveBeenCalled();
      expect(missionStatusRecordService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMissionStatusRecord>>();
      const missionStatusRecord = { id: 31132 };
      jest.spyOn(missionStatusRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ missionStatusRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(missionStatusRecordService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDailyMission', () => {
      it('should forward to dailyMissionService', () => {
        const entity = { id: 27924 };
        const entity2 = { id: 31998 };
        jest.spyOn(dailyMissionService, 'compareDailyMission');
        comp.compareDailyMission(entity, entity2);
        expect(dailyMissionService.compareDailyMission).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
