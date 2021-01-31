import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportStatusIndicatorComponent} from './import-status-indicator.component';

describe('ImportStatusIndicatorComponent', () => {
  let component: ImportStatusIndicatorComponent;
  let fixture: ComponentFixture<ImportStatusIndicatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportStatusIndicatorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportStatusIndicatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
