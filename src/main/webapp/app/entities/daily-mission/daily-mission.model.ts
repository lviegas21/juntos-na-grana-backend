import dayjs from 'dayjs/esm';
import { IFamily } from 'app/entities/family/family.model';
import { DailyMissionType } from 'app/entities/enumerations/daily-mission-type.model';
import { GoalCategory } from 'app/entities/enumerations/goal-category.model';

export interface IDailyMission {
  id: number;
  title?: string | null;
  description?: string | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  type?: keyof typeof DailyMissionType | null;
  targetAmount?: number | null;
  category?: keyof typeof GoalCategory | null;
  xpReward?: number | null;
  createdAt?: dayjs.Dayjs | null;
  family?: IFamily | null;
}

export type NewDailyMission = Omit<IDailyMission, 'id'> & { id: null };
