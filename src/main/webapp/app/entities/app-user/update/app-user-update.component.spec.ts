import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { AppUserService } from '../service/app-user.service';
import { IAppUser } from '../app-user.model';
import { AppUserFormService } from './app-user-form.service';

import { AppUserUpdateComponent } from './app-user-update.component';

describe('AppUser Management Update Component', () => {
  let comp: AppUserUpdateComponent;
  let fixture: ComponentFixture<AppUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appUserFormService: AppUserFormService;
  let appUserService: AppUserService;
  let familyService: FamilyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppUserUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AppUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appUserFormService = TestBed.inject(AppUserFormService);
    appUserService = TestBed.inject(AppUserService);
    familyService = TestBed.inject(FamilyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Family query and add missing value', () => {
      const appUser: IAppUser = { id: 16679 };
      const family: IFamily = { id: 15147 };
      appUser.family = family;

      const familyCollection: IFamily[] = [{ id: 15147 }];
      jest.spyOn(familyService, 'query').mockReturnValue(of(new HttpResponse({ body: familyCollection })));
      const additionalFamilies = [family];
      const expectedCollection: IFamily[] = [...additionalFamilies, ...familyCollection];
      jest.spyOn(familyService, 'addFamilyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(familyService.query).toHaveBeenCalled();
      expect(familyService.addFamilyToCollectionIfMissing).toHaveBeenCalledWith(
        familyCollection,
        ...additionalFamilies.map(expect.objectContaining),
      );
      expect(comp.familiesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const appUser: IAppUser = { id: 16679 };
      const family: IFamily = { id: 15147 };
      appUser.family = family;

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(comp.familiesSharedCollection).toContainEqual(family);
      expect(comp.appUser).toEqual(appUser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 14418 };
      jest.spyOn(appUserFormService, 'getAppUser').mockReturnValue(appUser);
      jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appUser }));
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(appUserService.update).toHaveBeenCalledWith(expect.objectContaining(appUser));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 14418 };
      jest.spyOn(appUserFormService, 'getAppUser').mockReturnValue({ id: null });
      jest.spyOn(appUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appUser }));
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(appUserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 14418 };
      jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appUserService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFamily', () => {
      it('should forward to familyService', () => {
        const entity = { id: 15147 };
        const entity2 = { id: 25490 };
        jest.spyOn(familyService, 'compareFamily');
        comp.compareFamily(entity, entity2);
        expect(familyService.compareFamily).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
