import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDailyMission } from 'app/entities/daily-mission/daily-mission.model';
import { DailyMissionService } from 'app/entities/daily-mission/service/daily-mission.service';
import { MissionStatusType } from 'app/entities/enumerations/mission-status-type.model';
import { MissionStatusRecordService } from '../service/mission-status-record.service';
import { IMissionStatusRecord } from '../mission-status-record.model';
import { MissionStatusRecordFormGroup, MissionStatusRecordFormService } from './mission-status-record-form.service';

@Component({
  selector: 'jhi-mission-status-record-update',
  templateUrl: './mission-status-record-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MissionStatusRecordUpdateComponent implements OnInit {
  isSaving = false;
  missionStatusRecord: IMissionStatusRecord | null = null;
  missionStatusTypeValues = Object.keys(MissionStatusType);

  dailyMissionsSharedCollection: IDailyMission[] = [];

  protected missionStatusRecordService = inject(MissionStatusRecordService);
  protected missionStatusRecordFormService = inject(MissionStatusRecordFormService);
  protected dailyMissionService = inject(DailyMissionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MissionStatusRecordFormGroup = this.missionStatusRecordFormService.createMissionStatusRecordFormGroup();

  compareDailyMission = (o1: IDailyMission | null, o2: IDailyMission | null): boolean =>
    this.dailyMissionService.compareDailyMission(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ missionStatusRecord }) => {
      this.missionStatusRecord = missionStatusRecord;
      if (missionStatusRecord) {
        this.updateForm(missionStatusRecord);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const missionStatusRecord = this.missionStatusRecordFormService.getMissionStatusRecord(this.editForm);
    if (missionStatusRecord.id !== null) {
      this.subscribeToSaveResponse(this.missionStatusRecordService.update(missionStatusRecord));
    } else {
      this.subscribeToSaveResponse(this.missionStatusRecordService.create(missionStatusRecord));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMissionStatusRecord>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(missionStatusRecord: IMissionStatusRecord): void {
    this.missionStatusRecord = missionStatusRecord;
    this.missionStatusRecordFormService.resetForm(this.editForm, missionStatusRecord);

    this.dailyMissionsSharedCollection = this.dailyMissionService.addDailyMissionToCollectionIfMissing<IDailyMission>(
      this.dailyMissionsSharedCollection,
      missionStatusRecord.mission,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.dailyMissionService
      .query()
      .pipe(map((res: HttpResponse<IDailyMission[]>) => res.body ?? []))
      .pipe(
        map((dailyMissions: IDailyMission[]) =>
          this.dailyMissionService.addDailyMissionToCollectionIfMissing<IDailyMission>(dailyMissions, this.missionStatusRecord?.mission),
        ),
      )
      .subscribe((dailyMissions: IDailyMission[]) => (this.dailyMissionsSharedCollection = dailyMissions));
  }
}
