import dayjs from 'dayjs/esm';

import { IDailyMission, NewDailyMission } from './daily-mission.model';

export const sampleWithRequiredData: IDailyMission = {
  id: 19220,
  title: 'innovation until',
  startDate: dayjs('2025-07-11T04:43'),
  endDate: dayjs('2025-07-10T16:11'),
  type: 'INVESTMENT',
  xpReward: 4008,
  createdAt: dayjs('2025-07-10T15:00'),
};

export const sampleWithPartialData: IDailyMission = {
  id: 22745,
  title: 'neatly afore',
  startDate: dayjs('2025-07-11T02:05'),
  endDate: dayjs('2025-07-11T01:15'),
  type: 'RESTRICTION',
  category: 'EQUIPMENT',
  xpReward: 4385,
  createdAt: dayjs('2025-07-11T08:04'),
};

export const sampleWithFullData: IDailyMission = {
  id: 32346,
  title: 'whoa delightfully',
  description: 'impartial shyly',
  startDate: dayjs('2025-07-11T06:06'),
  endDate: dayjs('2025-07-10T17:30'),
  type: 'TRACKING',
  targetAmount: 9020.97,
  category: 'UPGRADE',
  xpReward: 26354,
  createdAt: dayjs('2025-07-11T00:23'),
};

export const sampleWithNewData: NewDailyMission = {
  title: 'but',
  startDate: dayjs('2025-07-10T16:41'),
  endDate: dayjs('2025-07-11T11:44'),
  type: 'SAVING',
  xpReward: 3075,
  createdAt: dayjs('2025-07-11T00:51'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
