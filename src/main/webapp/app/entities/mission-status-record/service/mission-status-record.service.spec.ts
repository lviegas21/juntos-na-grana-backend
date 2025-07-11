import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMissionStatusRecord } from '../mission-status-record.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../mission-status-record.test-samples';

import { MissionStatusRecordService, RestMissionStatusRecord } from './mission-status-record.service';

const requireRestSample: RestMissionStatusRecord = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('MissionStatusRecord Service', () => {
  let service: MissionStatusRecordService;
  let httpMock: HttpTestingController;
  let expectedResult: IMissionStatusRecord | IMissionStatusRecord[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MissionStatusRecordService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a MissionStatusRecord', () => {
      const missionStatusRecord = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(missionStatusRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MissionStatusRecord', () => {
      const missionStatusRecord = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(missionStatusRecord).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MissionStatusRecord', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MissionStatusRecord', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MissionStatusRecord', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMissionStatusRecordToCollectionIfMissing', () => {
      it('should add a MissionStatusRecord to an empty array', () => {
        const missionStatusRecord: IMissionStatusRecord = sampleWithRequiredData;
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing([], missionStatusRecord);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(missionStatusRecord);
      });

      it('should not add a MissionStatusRecord to an array that contains it', () => {
        const missionStatusRecord: IMissionStatusRecord = sampleWithRequiredData;
        const missionStatusRecordCollection: IMissionStatusRecord[] = [
          {
            ...missionStatusRecord,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing(missionStatusRecordCollection, missionStatusRecord);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MissionStatusRecord to an array that doesn't contain it", () => {
        const missionStatusRecord: IMissionStatusRecord = sampleWithRequiredData;
        const missionStatusRecordCollection: IMissionStatusRecord[] = [sampleWithPartialData];
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing(missionStatusRecordCollection, missionStatusRecord);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(missionStatusRecord);
      });

      it('should add only unique MissionStatusRecord to an array', () => {
        const missionStatusRecordArray: IMissionStatusRecord[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const missionStatusRecordCollection: IMissionStatusRecord[] = [sampleWithRequiredData];
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing(missionStatusRecordCollection, ...missionStatusRecordArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const missionStatusRecord: IMissionStatusRecord = sampleWithRequiredData;
        const missionStatusRecord2: IMissionStatusRecord = sampleWithPartialData;
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing([], missionStatusRecord, missionStatusRecord2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(missionStatusRecord);
        expect(expectedResult).toContain(missionStatusRecord2);
      });

      it('should accept null and undefined values', () => {
        const missionStatusRecord: IMissionStatusRecord = sampleWithRequiredData;
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing([], null, missionStatusRecord, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(missionStatusRecord);
      });

      it('should return initial array if no MissionStatusRecord is added', () => {
        const missionStatusRecordCollection: IMissionStatusRecord[] = [sampleWithRequiredData];
        expectedResult = service.addMissionStatusRecordToCollectionIfMissing(missionStatusRecordCollection, undefined, null);
        expect(expectedResult).toEqual(missionStatusRecordCollection);
      });
    });

    describe('compareMissionStatusRecord', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMissionStatusRecord(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31132 };
        const entity2 = null;

        const compareResult1 = service.compareMissionStatusRecord(entity1, entity2);
        const compareResult2 = service.compareMissionStatusRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31132 };
        const entity2 = { id: 16586 };

        const compareResult1 = service.compareMissionStatusRecord(entity1, entity2);
        const compareResult2 = service.compareMissionStatusRecord(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31132 };
        const entity2 = { id: 31132 };

        const compareResult1 = service.compareMissionStatusRecord(entity1, entity2);
        const compareResult2 = service.compareMissionStatusRecord(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
