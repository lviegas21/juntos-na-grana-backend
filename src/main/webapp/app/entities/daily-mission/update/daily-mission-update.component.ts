import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { DailyMissionType } from 'app/entities/enumerations/daily-mission-type.model';
import { GoalCategory } from 'app/entities/enumerations/goal-category.model';
import { DailyMissionService } from '../service/daily-mission.service';
import { IDailyMission } from '../daily-mission.model';
import { DailyMissionFormGroup, DailyMissionFormService } from './daily-mission-form.service';

@Component({
  selector: 'jhi-daily-mission-update',
  templateUrl: './daily-mission-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DailyMissionUpdateComponent implements OnInit {
  isSaving = false;
  dailyMission: IDailyMission | null = null;
  dailyMissionTypeValues = Object.keys(DailyMissionType);
  goalCategoryValues = Object.keys(GoalCategory);

  familiesSharedCollection: IFamily[] = [];

  protected dailyMissionService = inject(DailyMissionService);
  protected dailyMissionFormService = inject(DailyMissionFormService);
  protected familyService = inject(FamilyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DailyMissionFormGroup = this.dailyMissionFormService.createDailyMissionFormGroup();

  compareFamily = (o1: IFamily | null, o2: IFamily | null): boolean => this.familyService.compareFamily(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dailyMission }) => {
      this.dailyMission = dailyMission;
      if (dailyMission) {
        this.updateForm(dailyMission);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dailyMission = this.dailyMissionFormService.getDailyMission(this.editForm);
    if (dailyMission.id !== null) {
      this.subscribeToSaveResponse(this.dailyMissionService.update(dailyMission));
    } else {
      this.subscribeToSaveResponse(this.dailyMissionService.create(dailyMission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDailyMission>>): void {
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

  protected updateForm(dailyMission: IDailyMission): void {
    this.dailyMission = dailyMission;
    this.dailyMissionFormService.resetForm(this.editForm, dailyMission);

    this.familiesSharedCollection = this.familyService.addFamilyToCollectionIfMissing<IFamily>(
      this.familiesSharedCollection,
      dailyMission.family,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.familyService
      .query()
      .pipe(map((res: HttpResponse<IFamily[]>) => res.body ?? []))
      .pipe(map((families: IFamily[]) => this.familyService.addFamilyToCollectionIfMissing<IFamily>(families, this.dailyMission?.family)))
      .subscribe((families: IFamily[]) => (this.familiesSharedCollection = families));
  }
}
