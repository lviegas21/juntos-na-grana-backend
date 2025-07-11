import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IFamily } from '../family.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../family.test-samples';

import { FamilyService, RestFamily } from './family.service';

const requireRestSample: RestFamily = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('Family Service', () => {
  let service: FamilyService;
  let httpMock: HttpTestingController;
  let expectedResult: IFamily | IFamily[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FamilyService);
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

    it('should create a Family', () => {
      const family = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(family).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Family', () => {
      const family = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(family).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Family', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Family', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Family', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFamilyToCollectionIfMissing', () => {
      it('should add a Family to an empty array', () => {
        const family: IFamily = sampleWithRequiredData;
        expectedResult = service.addFamilyToCollectionIfMissing([], family);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(family);
      });

      it('should not add a Family to an array that contains it', () => {
        const family: IFamily = sampleWithRequiredData;
        const familyCollection: IFamily[] = [
          {
            ...family,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFamilyToCollectionIfMissing(familyCollection, family);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Family to an array that doesn't contain it", () => {
        const family: IFamily = sampleWithRequiredData;
        const familyCollection: IFamily[] = [sampleWithPartialData];
        expectedResult = service.addFamilyToCollectionIfMissing(familyCollection, family);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(family);
      });

      it('should add only unique Family to an array', () => {
        const familyArray: IFamily[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const familyCollection: IFamily[] = [sampleWithRequiredData];
        expectedResult = service.addFamilyToCollectionIfMissing(familyCollection, ...familyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const family: IFamily = sampleWithRequiredData;
        const family2: IFamily = sampleWithPartialData;
        expectedResult = service.addFamilyToCollectionIfMissing([], family, family2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(family);
        expect(expectedResult).toContain(family2);
      });

      it('should accept null and undefined values', () => {
        const family: IFamily = sampleWithRequiredData;
        expectedResult = service.addFamilyToCollectionIfMissing([], null, family, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(family);
      });

      it('should return initial array if no Family is added', () => {
        const familyCollection: IFamily[] = [sampleWithRequiredData];
        expectedResult = service.addFamilyToCollectionIfMissing(familyCollection, undefined, null);
        expect(expectedResult).toEqual(familyCollection);
      });
    });

    describe('compareFamily', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFamily(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15147 };
        const entity2 = null;

        const compareResult1 = service.compareFamily(entity1, entity2);
        const compareResult2 = service.compareFamily(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15147 };
        const entity2 = { id: 25490 };

        const compareResult1 = service.compareFamily(entity1, entity2);
        const compareResult2 = service.compareFamily(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15147 };
        const entity2 = { id: 15147 };

        const compareResult1 = service.compareFamily(entity1, entity2);
        const compareResult2 = service.compareFamily(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
