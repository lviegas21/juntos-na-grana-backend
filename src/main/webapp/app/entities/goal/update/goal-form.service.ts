import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IGoal, NewGoal } from '../goal.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGoal for edit and NewGoalFormGroupInput for create.
 */
type GoalFormGroupInput = IGoal | PartialWithRequiredKeyOf<NewGoal>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGoal | NewGoal> = Omit<T, 'createdAt' | 'dueDate'> & {
  createdAt?: string | null;
  dueDate?: string | null;
};

type GoalFormRawValue = FormValueOf<IGoal>;

type NewGoalFormRawValue = FormValueOf<NewGoal>;

type GoalFormDefaults = Pick<NewGoal, 'id' | 'createdAt' | 'dueDate' | 'alertEnabled'>;

type GoalFormGroupContent = {
  id: FormControl<GoalFormRawValue['id'] | NewGoal['id']>;
  title: FormControl<GoalFormRawValue['title']>;
  description: FormControl<GoalFormRawValue['description']>;
  targetAmount: FormControl<GoalFormRawValue['targetAmount']>;
  currentAmount: FormControl<GoalFormRawValue['currentAmount']>;
  createdAt: FormControl<GoalFormRawValue['createdAt']>;
  dueDate: FormControl<GoalFormRawValue['dueDate']>;
  category: FormControl<GoalFormRawValue['category']>;
  priority: FormControl<GoalFormRawValue['priority']>;
  alertEnabled: FormControl<GoalFormRawValue['alertEnabled']>;
  alertThreshold: FormControl<GoalFormRawValue['alertThreshold']>;
  family: FormControl<GoalFormRawValue['family']>;
};

export type GoalFormGroup = FormGroup<GoalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GoalFormService {
  createGoalFormGroup(goal: GoalFormGroupInput = { id: null }): GoalFormGroup {
    const goalRawValue = this.convertGoalToGoalRawValue({
      ...this.getFormDefaults(),
      ...goal,
    });
    return new FormGroup<GoalFormGroupContent>({
      id: new FormControl(
        { value: goalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(goalRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(goalRawValue.description),
      targetAmount: new FormControl(goalRawValue.targetAmount, {
        validators: [Validators.required],
      }),
      currentAmount: new FormControl(goalRawValue.currentAmount, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(goalRawValue.createdAt, {
        validators: [Validators.required],
      }),
      dueDate: new FormControl(goalRawValue.dueDate),
      category: new FormControl(goalRawValue.category, {
        validators: [Validators.required],
      }),
      priority: new FormControl(goalRawValue.priority, {
        validators: [Validators.required],
      }),
      alertEnabled: new FormControl(goalRawValue.alertEnabled, {
        validators: [Validators.required],
      }),
      alertThreshold: new FormControl(goalRawValue.alertThreshold, {
        validators: [Validators.required],
      }),
      family: new FormControl(goalRawValue.family, {
        validators: [Validators.required],
      }),
    });
  }

  getGoal(form: GoalFormGroup): IGoal | NewGoal {
    return this.convertGoalRawValueToGoal(form.getRawValue() as GoalFormRawValue | NewGoalFormRawValue);
  }

  resetForm(form: GoalFormGroup, goal: GoalFormGroupInput): void {
    const goalRawValue = this.convertGoalToGoalRawValue({ ...this.getFormDefaults(), ...goal });
    form.reset(
      {
        ...goalRawValue,
        id: { value: goalRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GoalFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      dueDate: currentTime,
      alertEnabled: false,
    };
  }

  private convertGoalRawValueToGoal(rawGoal: GoalFormRawValue | NewGoalFormRawValue): IGoal | NewGoal {
    return {
      ...rawGoal,
      createdAt: dayjs(rawGoal.createdAt, DATE_TIME_FORMAT),
      dueDate: dayjs(rawGoal.dueDate, DATE_TIME_FORMAT),
    };
  }

  private convertGoalToGoalRawValue(
    goal: IGoal | (Partial<NewGoal> & GoalFormDefaults),
  ): GoalFormRawValue | PartialWithRequiredKeyOf<NewGoalFormRawValue> {
    return {
      ...goal,
      createdAt: goal.createdAt ? goal.createdAt.format(DATE_TIME_FORMAT) : undefined,
      dueDate: goal.dueDate ? goal.dueDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
