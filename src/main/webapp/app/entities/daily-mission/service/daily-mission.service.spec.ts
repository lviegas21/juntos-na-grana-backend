import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDailyMission } from '../daily-mission.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../daily-mission.test-samples';

import { DailyMissionService, RestDailyMission } from './daily-mission.service';

const requireRestSample: RestDailyMission = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('DailyMission Service', () => {
  let service: DailyMissionService;
  let httpMock: HttpTestingController;
  let expectedResult: IDailyMission | IDailyMission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DailyMissionService);
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

    it('should create a DailyMission', () => {
      const dailyMission = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dailyMission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DailyMission', () => {
      const dailyMission = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dailyMission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DailyMission', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DailyMission', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DailyMission', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDailyMissionToCollectionIfMissing', () => {
      it('should add a DailyMission to an empty array', () => {
        const dailyMission: IDailyMission = sampleWithRequiredData;
        expectedResult = service.addDailyMissionToCollectionIfMissing([], dailyMission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyMission);
      });

      it('should not add a DailyMission to an array that contains it', () => {
        const dailyMission: IDailyMission = sampleWithRequiredData;
        const dailyMissionCollection: IDailyMission[] = [
          {
            ...dailyMission,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDailyMissionToCollectionIfMissing(dailyMissionCollection, dailyMission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DailyMission to an array that doesn't contain it", () => {
        const dailyMission: IDailyMission = sampleWithRequiredData;
        const dailyMissionCollection: IDailyMission[] = [sampleWithPartialData];
        expectedResult = service.addDailyMissionToCollectionIfMissing(dailyMissionCollection, dailyMission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyMission);
      });

      it('should add only unique DailyMission to an array', () => {
        const dailyMissionArray: IDailyMission[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dailyMissionCollection: IDailyMission[] = [sampleWithRequiredData];
        expectedResult = service.addDailyMissionToCollectionIfMissing(dailyMissionCollection, ...dailyMissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dailyMission: IDailyMission = sampleWithRequiredData;
        const dailyMission2: IDailyMission = sampleWithPartialData;
        expectedResult = service.addDailyMissionToCollectionIfMissing([], dailyMission, dailyMission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyMission);
        expect(expectedResult).toContain(dailyMission2);
      });

      it('should accept null and undefined values', () => {
        const dailyMission: IDailyMission = sampleWithRequiredData;
        expectedResult = service.addDailyMissionToCollectionIfMissing([], null, dailyMission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyMission);
      });

      it('should return initial array if no DailyMission is added', () => {
        const dailyMissionCollection: IDailyMission[] = [sampleWithRequiredData];
        expectedResult = service.addDailyMissionToCollectionIfMissing(dailyMissionCollection, undefined, null);
        expect(expectedResult).toEqual(dailyMissionCollection);
      });
    });

    describe('compareDailyMission', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDailyMission(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27924 };
        const entity2 = null;

        const compareResult1 = service.compareDailyMission(entity1, entity2);
        const compareResult2 = service.compareDailyMission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27924 };
        const entity2 = { id: 31998 };

        const compareResult1 = service.compareDailyMission(entity1, entity2);
        const compareResult2 = service.compareDailyMission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27924 };
        const entity2 = { id: 27924 };

        const compareResult1 = service.compareDailyMission(entity1, entity2);
        const compareResult2 = service.compareDailyMission(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
