import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ImportSetupComponent} from './import-setup.component';

describe('ImportSetupComponent', () => {
  let component: ImportSetupComponent;
  let fixture: ComponentFixture<ImportSetupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImportSetupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
