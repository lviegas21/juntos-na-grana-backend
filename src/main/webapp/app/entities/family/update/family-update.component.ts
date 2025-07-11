import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFamily } from '../family.model';
import { FamilyService } from '../service/family.service';
import { FamilyFormGroup, FamilyFormService } from './family-form.service';

@Component({
  selector: 'jhi-family-update',
  templateUrl: './family-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FamilyUpdateComponent implements OnInit {
  isSaving = false;
  family: IFamily | null = null;

  protected familyService = inject(FamilyService);
  protected familyFormService = inject(FamilyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FamilyFormGroup = this.familyFormService.createFamilyFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ family }) => {
      this.family = family;
      if (family) {
        this.updateForm(family);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const family = this.familyFormService.getFamily(this.editForm);
    if (family.id !== null) {
      this.subscribeToSaveResponse(this.familyService.update(family));
    } else {
      this.subscribeToSaveResponse(this.familyService.create(family));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFamily>>): void {
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

  protected updateForm(family: IFamily): void {
    this.family = family;
    this.familyFormService.resetForm(this.editForm, family);
  }
}
