import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { UserService } from '../../../../core/services/user.service';
import { UserInformation } from '../../../../shared/models/user-information.models';
import { finalize } from 'rxjs';

interface PurchaseHistoryItem {
  id: number;
  candleName: string;
  price: number;
  quantity: number;
  purchasedAt: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  user: UserInformation | null = null;

  readonly isEditing = signal(false);
  readonly isSaving = signal(false);
  readonly isUploadingImage = signal(false);

  defaultImage = 'https://www.freeiconspng.com/uploads/camera-icon-21.png';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUserInformation();
  }

  readonly profileForm = this.fb.nonNullable.group({
    username: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(80),
    ]),
    imageUrl: this.fb.nonNullable.control(
      'https://www.freeiconspng.com/uploads/camera-icon-21.png',
    ),
  });

  private initialProfileSnapshot = this.profileForm.getRawValue();

  readonly purchaseHistory = signal<PurchaseHistoryItem[]>([
    {
      id: 1,
      candleName: 'Vela Aromatica Lavanda',
      price: 42000,
      quantity: 2,
      purchasedAt: '2026-04-20',
    },
    {
      id: 2,
      candleName: 'Vela Clasica Vainilla',
      price: 31000,
      quantity: 1,
      purchasedAt: '2026-04-14',
    },
    {
      id: 3,
      candleName: 'Vela Decorativa Canela',
      price: 39000,
      quantity: 3,
      purchasedAt: '2026-04-02',
    },
    {
      id: 4,
      candleName: 'Vela Romantic Rose',
      price: 47000,
      quantity: 1,
      purchasedAt: '2026-03-27',
    },
    {
      id: 5,
      candleName: 'Vela Essential Balance',
      price: 51000,
      quantity: 2,
      purchasedAt: '2026-03-15',
    },
  ]);

  readonly fullName = computed(() => {
    const { username } = this.profileForm.getRawValue();
    return username.trim();
  });

  readonly totalSpent = computed(() =>
    this.purchaseHistory().reduce(
      (acc, item) => acc + item.price * item.quantity,
      0,
    ),
  );

  startEdit(): void {
    this.isEditing.set(true);
  }

  cancelEdit(): void {
    this.profileForm.reset(this.initialProfileSnapshot);
    this.isEditing.set(false);
  }

  saveChanges(): void {
    if (this.profileForm.controls.username.invalid) {
      this.profileForm.controls.username.markAsTouched();
      return;
    }

    const username = this.profileForm.controls.username.value.trim();
    if (!username) {
      this.profileForm.controls.username.markAsTouched();
      return;
    }

    this.isSaving.set(true);
    this.userService
      .updateUsername(username)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: (response: UserInformation) => {
          this.user = response;
          this.patchProfileForm(response);
          this.isEditing.set(false);
        },
        error: () => {
          this.profileForm.controls.username.markAsTouched();
        },
      });
  }

  onImageSelected(event: Event): void {
    if (!this.isEditing()) return;

    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const key = this.buildProfileImageKey(file);
    this.isUploadingImage.set(true);
    this.userService
      .updateProfileImage(file, key)
      .pipe(
        finalize(() => {
          this.isUploadingImage.set(false);
          input.value = '';
        }),
      )
      .subscribe({
        next: () => {
          // The backend stores the key and returns text response,
          // so we re-fetch user information to get a fresh presigned URL.
          this.loadUserInformation();
        },
        error: () => {},
      });
  }

  private loadUserInformation(): void {
    this.userService.getUserInformation().subscribe({
      next: (response: UserInformation) => {
        this.user = response;
        this.patchProfileForm(response);
      },
      error: () => {},
    });
  }

  private patchProfileForm(user: UserInformation): void {
    this.profileForm.patchValue({
      username: user.username ?? '',
      imageUrl: user.imageUrl || 'assets/default-avatar.png',
    });
    this.initialProfileSnapshot = this.profileForm.getRawValue();
  }

  private buildProfileImageKey(file: File): string {
    const extension = file.name.includes('.')
      ? file.name.split('.').pop()
      : 'jpg';
    const safeUsername = (this.profileForm.controls.username.value || 'user')
      .trim()
      .toLowerCase()
      .replace(/[^a-z0-9-_]/g, '-');
    return `profiles/${safeUsername}-${Date.now()}.${extension}`;
  }

  getImageUrl(): string {
    const url = this.user?.imageUrl;

    if (!url || url === 'null' || url === 'undefined') {
      return this.defaultImage;
    }

    return url;
  }
}
