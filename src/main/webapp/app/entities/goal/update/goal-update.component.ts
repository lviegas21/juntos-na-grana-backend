import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { GoalCategory } from 'app/entities/enumerations/goal-category.model';
import { GoalPriority } from 'app/entities/enumerations/goal-priority.model';
import { GoalService } from '../service/goal.service';
import { IGoal } from '../goal.model';
import { GoalFormGroup, GoalFormService } from './goal-form.service';

@Component({
  selector: 'jhi-goal-update',
  templateUrl: './goal-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GoalUpdateComponent implements OnInit {
  isSaving = false;
  goal: IGoal | null = null;
  goalCategoryValues = Object.keys(GoalCategory);
  goalPriorityValues = Object.keys(GoalPriority);

  familiesSharedCollection: IFamily[] = [];

  protected goalService = inject(GoalService);
  protected goalFormService = inject(GoalFormService);
  protected familyService = inject(FamilyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: GoalFormGroup = this.goalFormService.createGoalFormGroup();

  compareFamily = (o1: IFamily | null, o2: IFamily | null): boolean => this.familyService.compareFamily(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ goal }) => {
      this.goal = goal;
      if (goal) {
        this.updateForm(goal);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const goal = this.goalFormService.getGoal(this.editForm);
    if (goal.id !== null) {
      this.subscribeToSaveResponse(this.goalService.update(goal));
    } else {
      this.subscribeToSaveResponse(this.goalService.create(goal));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGoal>>): void {
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

  protected updateForm(goal: IGoal): void {
    this.goal = goal;
    this.goalFormService.resetForm(this.editForm, goal);

    this.familiesSharedCollection = this.familyService.addFamilyToCollectionIfMissing<IFamily>(this.familiesSharedCollection, goal.family);
  }

  protected loadRelationshipsOptions(): void {
    this.familyService
      .query()
      .pipe(map((res: HttpResponse<IFamily[]>) => res.body ?? []))
      .pipe(map((families: IFamily[]) => this.familyService.addFamilyToCollectionIfMissing<IFamily>(families, this.goal?.family)))
      .subscribe((families: IFamily[]) => (this.familiesSharedCollection = families));
  }
}
