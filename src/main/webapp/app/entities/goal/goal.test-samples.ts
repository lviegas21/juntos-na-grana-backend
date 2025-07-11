import dayjs from 'dayjs/esm';

import { IGoal, NewGoal } from './goal.model';

export const sampleWithRequiredData: IGoal = {
  id: 3744,
  title: 'obedience eek misread',
  targetAmount: 29144.39,
  currentAmount: 22612.78,
  createdAt: dayjs('2025-07-11T04:40'),
  category: 'POTION',
  priority: 'HIGH',
  alertEnabled: true,
  alertThreshold: 13018,
};

export const sampleWithPartialData: IGoal = {
  id: 12155,
  title: 'really fluctuate',
  description: 'aw',
  targetAmount: 23209.72,
  currentAmount: 2969.33,
  createdAt: dayjs('2025-07-10T23:45'),
  dueDate: dayjs('2025-07-11T06:22'),
  category: 'ADVENTURE',
  priority: 'HIGH',
  alertEnabled: true,
  alertThreshold: 27857,
};

export const sampleWithFullData: IGoal = {
  id: 13490,
  title: 'aw tankful contractor',
  description: 'than and repeatedly',
  targetAmount: 20569.55,
  currentAmount: 17010.36,
  createdAt: dayjs('2025-07-11T00:48'),
  dueDate: dayjs('2025-07-10T23:12'),
  category: 'EQUIPMENT',
  priority: 'LOW',
  alertEnabled: false,
  alertThreshold: 14298,
};

export const sampleWithNewData: NewGoal = {
  title: 'notwithstanding at',
  targetAmount: 20732.27,
  currentAmount: 19179.63,
  createdAt: dayjs('2025-07-11T11:30'),
  category: 'OTHER',
  priority: 'MEDIUM',
  alertEnabled: true,
  alertThreshold: 13732,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
