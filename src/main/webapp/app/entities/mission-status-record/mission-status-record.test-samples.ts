import dayjs from 'dayjs/esm';

import { IMissionStatusRecord, NewMissionStatusRecord } from './mission-status-record.model';

export const sampleWithRequiredData: IMissionStatusRecord = {
  id: 6868,
  date: dayjs('2025-07-11T01:23'),
  statusType: 'FAILED',
};

export const sampleWithPartialData: IMissionStatusRecord = {
  id: 27051,
  date: dayjs('2025-07-10T16:06'),
  statusType: 'FAILED',
};

export const sampleWithFullData: IMissionStatusRecord = {
  id: 3094,
  date: dayjs('2025-07-11T03:32'),
  statusType: 'COMPLETED',
};

export const sampleWithNewData: NewMissionStatusRecord = {
  date: dayjs('2025-07-10T19:27'),
  statusType: 'FAILED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
