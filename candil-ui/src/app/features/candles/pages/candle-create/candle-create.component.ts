import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {
  CATEGORIES,
  CategoryEnum,
  FEATURES,
  MATERIALS,
  MaterialEnum,
  FeatureEnum,
  CandleRequest,
} from '../../../../shared/models/candle.models';

import { IngredientCatalogResponse } from '../../../../shared/models/ingredient.models';
import { IngredientCatalogService } from '../../../../core/services/ingredient-catalog.service';

import { CandleService } from '../../../../core/services/candle.service';

type IngredientFormGroup = FormGroup<{
  ingredientId: FormControl<number>;
  amount: FormControl<number>;
}>;

@Component({
  selector: 'app-candle-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
  ],
  templateUrl: './candle-create.component.html',
  styleUrl: './candle-create.component.css',
})
export class CandleCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly candleService = inject(CandleService);
  private readonly ingredientCatalogService = inject(IngredientCatalogService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly categories = CATEGORIES;
  readonly materials = MATERIALS;
  readonly features = FEATURES;

  readonly ingredientOptions = signal<IngredientCatalogResponse[]>([]);

  readonly creating = signal(false);
  readonly principalImageFile = signal<File | null>(null);
  readonly additionalImageFiles = signal<File[]>([]);

  readonly ingredientDraftForm = this.fb.group({
    ingredientId: this.fb.control<number | null>(null, Validators.required),
    amount: this.fb.control<number | null>(null, [
      Validators.required,
      Validators.min(0.01),
    ]),
  });

  readonly form = this.fb.nonNullable.group({
    name: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(120),
    ]),
    description: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(900),
    ]),
    stock: this.fb.nonNullable.control<number | null>(null, [
      Validators.required,
      Validators.min(0),
    ]),
    materialEnums: this.fb.nonNullable.control<MaterialEnum[]>(
      [],
      [Validators.required],
    ),
    featureEnums: this.fb.nonNullable.control<FeatureEnum[]>(
      [],
      [Validators.required],
    ),
    categories: this.fb.nonNullable.control<CategoryEnum[]>(
      [],
      [Validators.required],
    ),
    ingredients: this.fb.array<IngredientFormGroup>([]),
  });

  constructor() {
    this.ingredientCatalogService.findAll().subscribe({
      next: (ingredients) =>
        this.ingredientOptions.set(ingredients.filter((i) => i.active)),
    });
  }

  get ingredientsArray(): FormArray<IngredientFormGroup> {
    return this.form.controls.ingredients;
  }

  onPrincipalImageChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.principalImageFile.set(file);
  }

  onAdditionalImagesChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files ?? []);
    this.additionalImageFiles.set(files);
  }

  removeAdditionalImage(index: number): void {
    const current = [...this.additionalImageFiles()];
    current.splice(index, 1);
    this.additionalImageFiles.set(current);
  }

  addIngredient(): void {
    if (this.ingredientDraftForm.invalid) {
      this.ingredientDraftForm.markAllAsTouched();
      return;
    }

    const { ingredientId, amount } = this.ingredientDraftForm.getRawValue();

    if (ingredientId === null || amount === null) return;

    this.ingredientsArray.push(this.buildIngredientGroup(ingredientId, amount));
    this.ingredientDraftForm.reset();
  }

  removeIngredient(index: number): void {
    this.ingredientsArray.removeAt(index);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.principalImageFile()) {
      this.snackBar.open('La imagen principal es obligatoria.', 'Cerrar', {
        duration: 3000,
      });
      return;
    }

    const principalImage = this.principalImageFile()!;
    const additionalImages = this.additionalImageFiles();
    const rawValue = this.form.getRawValue();

    const payload: CandleRequest = {
      ...rawValue,
      principalImage: principalImage.name,
      images: additionalImages.map((file) => file.name),
      stock: rawValue.stock ?? 0,
    };

    this.creating.set(true);
    this.candleService
      .create(payload, principalImage, additionalImages)
      .subscribe({
        next: () => {
          this.creating.set(false);
          this.snackBar.open('Vela creada correctamente', 'Cerrar', {
            duration: 2500,
          });
          this.router.navigate(['/candles']);
          console.log(payload);
        },
        error: () => {
          this.creating.set(false);
          console.log(payload);
          this.snackBar.open(
            'No se pudo crear la vela. Revisa los datos e intenta de nuevo.',
            'Cerrar',
            { duration: 3500 },
          );
        },
      });
  }

  formatLabel(value: string): string {
    return value.replace(/_/g, ' ');
  }

  private buildIngredientGroup(
    ingredientId: number,
    amount: number,
  ): IngredientFormGroup {
    return this.fb.nonNullable.group({
      ingredientId: this.fb.nonNullable.control(
        ingredientId,
        Validators.required,
      ),
      amount: this.fb.nonNullable.control(amount, [
        Validators.required,
        Validators.min(0.01),
      ]),
    });
  }

  getLabel(value: string, options: { value: string; label: string }[]): string {
    return options.find((option) => option.value === value)?.label ?? value;
  }

  ingredientName(id: number): string {
    return (
      this.ingredientOptions().find((i) => i.id === id)?.ingredientName ?? ''
    );
  }
}
