import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectPersonDialogComponent} from './select-person-dialog.component';

describe('SelectPersonDialogComponent', () => {
  let component: SelectPersonDialogComponent;
  let fixture: ComponentFixture<SelectPersonDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectPersonDialogComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectPersonDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
