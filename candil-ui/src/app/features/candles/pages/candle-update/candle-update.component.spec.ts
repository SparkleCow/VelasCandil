import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandleUpdateComponent } from './candle-update.component';

describe('CandleUpdateComponent', () => {
  let component: CandleUpdateComponent;
  let fixture: ComponentFixture<CandleUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandleUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CandleUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
