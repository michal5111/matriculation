import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ImportService} from "../../../service/import-service/import.service";
import {Page} from "../../../model/oracle/page/page";
import {filter, flatMap, map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {Application} from "../../../model/irk/application";
import {ActivatedRoute} from "@angular/router";
import {ImportProgress} from "../../../model/import/import-progress";
import {Observable, Subscription, timer} from "rxjs";
import {Import} from "../../../model/import/import";
import {UserService} from "../../../service/user-service/user.service";
import {Document} from "../../../model/irk/document";
import {MatDialog} from "@angular/material/dialog";
import {UpdateIndexNumberDialogComponent} from "../../dialog/update-index-number-dialog/update-index-number-dialog.component";
import {ConfirmationDialogComponent} from "../../dialog/confirmation-dialog/confirmation-dialog.component";
import {ConfirmationDialogData} from "../../../model/dialog/confirmation-dialog-data";

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass']
})
export class ImportViewComponent implements OnInit, OnDestroy {

  importId: number;
  import: Import;
  importProgress: ImportProgress;
  progressSubscription: Subscription;
  pageSize: number = 15;
  pageNumber: number = 0;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString: string = 'id';
  sortDirString: string = 'asc';
  displayedColumns: string[] = [
    'lp',
    'id',
    'irkId',
    'usosId',
    'names',
    'birthDateAndPlace',
    'pesel',
    'secondarySchoolDocumentNumber',
    'secondarySchoolDocumentIssueInstitution',
    'secondarySchoolDocumentIssueDate',
    'diplomaSchoolDocumentNumber',
    'diplomaSchoolDocumentIssueDateAndPlace',
    'diplomaSchoolDocumentIssueInstitution',
    'indexNumber',
    'applicationImportStatus',
    'importError'
  ];
  sortingMap: Map<string, string> = new Map<string, string>([
    ['id','id'],
    ['irkId','irkId'],
    ['usosId','applicant.usosId'],
    ['names','applicant.name.family'],
    ['birthDateAndPlace','applicant.basicData.dateOfBirth'],
    ['pesel','applicant.basicData.pesel'],
    ['indexNumber','applicant.assignedIndexNumber'],
    ['applicationImportStatus','applicationImportStatus'],
    ['importError','importError']
  ])
  $importProgressObservable = timer(0, 1000).pipe(
    flatMap(() => this.importService.getImportProgress(this.importId)),
    tap(result => this.importProgress = result),
    flatMap(() => this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString)),
    tap(() => {
      if (this.importProgress.importStatus == "IMPORTED"
        || this.importProgress.importStatus == "COMPLETE"
        || this.importProgress.importStatus == "PENDING"
        || this.importProgress.importStatus == "ARCHIVED") {
        this.progressSubscription.unsubscribe()
      }
    })
  );

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(
    private importService: ImportService,
    private route: ActivatedRoute,
    public _userService: UserService,
    private dialog: MatDialog
  ) {
  }

  getPage(page: number, size: number, sort?: string, sortDir?: string) {
    return this.importService.findAllApplicationsByImportId(this.importId, page, size, sort, sortDir)
      .pipe(
        tap(page => this.page = page),
        map(page => page.content),
        tap(results => this.dataSource.data = results)
      )
  }

  getImport(importId: number): Observable<Import> {
    return this.importService.getImport(importId).pipe(
      tap(importObject => this.import = importObject)
    )
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort
    this.importId = this.route.snapshot.params.id;
    if (!this.importId) {
      return
    }
    this.getImport(this.importId).subscribe()
    this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe(() => {
      this.progressSubscription = this.$importProgressObservable.subscribe()
    });
  }

  switchPage(pageEvent: PageEvent) {
    this.pageSize = pageEvent.pageSize
    this.pageNumber = pageEvent.pageIndex
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = this.sortingMap.get(sortEvent.active);
    this.sortDirString = sortEvent.direction;
    this.getPage(this.pageNumber, this.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  startImport() {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.startImport(this.importId).pipe(
      flatMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  savePersons() {
    this.progressSubscription.unsubscribe();
    this.progressSubscription = this.importService.savePersons(this.importId).pipe(
      flatMap(() => this.$importProgressObservable)
    ).subscribe();
  }

  getSecondarySchoolDocument(application: Application): Document {
    return application.applicant.educationData.documents
      .find(document => document.certificateUsosCode === 'D'
        || document.certificateUsosCode === 'N'
        || document.certificateUsosCode === 'E')
  }

  getDiplomaDocument(application: Application): Document {
    return application.applicant.educationData.documents
      .find(document => document.certificateUsosCode === 'L'
        || document.certificateUsosCode === 'I'
      )
  }

  updateIndexNumber(application: Application) {
    const dialogRef = this.dialog.open(UpdateIndexNumberDialogComponent, {
      width: '300px',
      height: '250px',
      data: {
        personId: application.applicant.usosId,
        indexTypeCode: this.import.indexPoolCode,
        indexNumber: application.applicant.assignedIndexNumber
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== '') {
        application.applicant.assignedIndexNumber = result
      }
    })
  }

  getElementNumber(application: Application): number {
    return this.page.content.indexOf(application) + this.pageSize * this.page.number + 1
  }

  getImportProgressPercentage(): number {
    return this.importProgress.importedApplications * 100 / this.importProgress.totalCount
  }

  getSaveProgressPercentage(): number {
    return (this.importProgress.savedApplicants + this.importProgress.saveErrors) * 100 / this.importProgress.totalCount
  }

  onArchiveClick() {
    const data = new ConfirmationDialogData("Archiwizuj import", "Czy na pewno chcesz zarchiwizować import? Procesu nie można odwrócić!")
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: data
    })
    dialogRef.afterClosed().pipe(
      filter(result => result === true),
      flatMap((result) => this.importService.archiveImport(this.importId)),
      flatMap(() => this.getImport(this.importId))
    ).subscribe()
  }

  ngOnDestroy(): void {
    this.progressSubscription.unsubscribe()
  }
}
