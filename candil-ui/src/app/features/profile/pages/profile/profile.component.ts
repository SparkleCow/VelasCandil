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
import { OrderService } from '../../../../core/services/order.service';
import { OrderResponseDto } from '../../../../shared/models/order.models';
import { finalize } from 'rxjs';

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
  private readonly orderService = inject(OrderService);

  user: UserInformation | null = null;

  readonly isEditing = signal(false);
  readonly isSaving = signal(false);
  readonly isUploadingImage = signal(false);

  defaultImage = 'https://www.freeiconspng.com/uploads/camera-icon-21.png';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUserInformation();
    this.loadOrders();
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

  readonly orders = signal<OrderResponseDto[]>([]);

  readonly fullName = computed(() => {
    const { username } = this.profileForm.getRawValue();
    return username.trim();
  });

  readonly totalSpent = computed(() =>
    this.orders().reduce((acc, order) => acc + order.total, 0),
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

  private loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => this.orders.set(orders),
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