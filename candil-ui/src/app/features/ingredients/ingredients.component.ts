import { CommonModule } from '@angular/common';
import {
  Component,
  ElementRef,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { RouterModule } from '@angular/router';
import { IngredientCatalogService } from '../../core/services/ingredient-catalog.service';

@Component({
  selector: 'app-ingredients',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    RouterModule,
  ],
  templateUrl: './ingredients.component.html',
  styleUrl: './ingredients.component.css',
})
export class IngredientsComponent {
  @ViewChild('fileInput')
  fileInput!: ElementRef<HTMLInputElement>;

  private readonly ingredientService = inject(IngredientCatalogService);
  private readonly snackBar = inject(MatSnackBar);

  readonly importing = signal(false);
  readonly selectedFile = signal<File | null>(null);

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      this.selectedFile.set(null);
      return;
    }

    this.selectedFile.set(input.files[0]);
  }

  importIngredient(): void {
    const file = this.selectedFile();

    if (!file) {
      return;
    }

    this.importing.set(true);

    this.ingredientService.importIngredients(file).subscribe({
      next: () => {
        this.importing.set(false);
        this.selectedFile.set(null);

        if (this.fileInput) {
          this.fileInput.nativeElement.value = '';
        }

        this.snackBar.open('Ingredientes importados correctamente.', 'Cerrar', {
          duration: 3000,
        });
      },
      error: () => {
        this.importing.set(false);

        this.snackBar.open('No fue posible importar el archivo.', 'Cerrar', {
          duration: 4000,
        });
      },
    });
  }
}
