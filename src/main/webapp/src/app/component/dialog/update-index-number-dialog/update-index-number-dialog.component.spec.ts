import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {UpdateIndexNumberDialogComponent} from './update-index-number-dialog.component';

describe('UpdateIndexNumberDialogComponent', () => {
  let component: UpdateIndexNumberDialogComponent;
  let fixture: ComponentFixture<UpdateIndexNumberDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateIndexNumberDialogComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateIndexNumberDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
