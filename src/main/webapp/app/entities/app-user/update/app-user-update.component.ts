import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';
import { AppUserFormGroup, AppUserFormService } from './app-user-form.service';

@Component({
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AppUserUpdateComponent implements OnInit {
  isSaving = false;
  appUser: IAppUser | null = null;

  familiesSharedCollection: IFamily[] = [];

  protected appUserService = inject(AppUserService);
  protected appUserFormService = inject(AppUserFormService);
  protected familyService = inject(FamilyService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AppUserFormGroup = this.appUserFormService.createAppUserFormGroup();

  compareFamily = (o1: IFamily | null, o2: IFamily | null): boolean => this.familyService.compareFamily(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      this.appUser = appUser;
      if (appUser) {
        this.updateForm(appUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appUser = this.appUserFormService.getAppUser(this.editForm);
    if (appUser.id !== null) {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(appUser: IAppUser): void {
    this.appUser = appUser;
    this.appUserFormService.resetForm(this.editForm, appUser);

    this.familiesSharedCollection = this.familyService.addFamilyToCollectionIfMissing<IFamily>(
      this.familiesSharedCollection,
      appUser.family,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.familyService
      .query()
      .pipe(map((res: HttpResponse<IFamily[]>) => res.body ?? []))
      .pipe(map((families: IFamily[]) => this.familyService.addFamilyToCollectionIfMissing<IFamily>(families, this.appUser?.family)))
      .subscribe((families: IFamily[]) => (this.familiesSharedCollection = families));
  }
}
