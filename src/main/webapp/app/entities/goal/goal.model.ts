import dayjs from 'dayjs/esm';
import { IFamily } from 'app/entities/family/family.model';
import { GoalCategory } from 'app/entities/enumerations/goal-category.model';
import { GoalPriority } from 'app/entities/enumerations/goal-priority.model';

export interface IGoal {
  id: number;
  title?: string | null;
  description?: string | null;
  targetAmount?: number | null;
  currentAmount?: number | null;
  createdAt?: dayjs.Dayjs | null;
  dueDate?: dayjs.Dayjs | null;
  category?: keyof typeof GoalCategory | null;
  priority?: keyof typeof GoalPriority | null;
  alertEnabled?: boolean | null;
  alertThreshold?: number | null;
  family?: IFamily | null;
}

export type NewGoal = Omit<IGoal, 'id'> & { id: null };
