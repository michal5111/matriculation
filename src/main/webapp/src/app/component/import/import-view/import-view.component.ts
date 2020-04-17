import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {ImportService} from "../../../service/import-service/import.service";
import {Page} from "../../../model/oracle/page/page";
import {flatMap, map, tap} from "rxjs/operators";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatSort, Sort} from "@angular/material/sort";
import {Application} from "../../../model/irk/application";
import {ActivatedRoute} from "@angular/router";
import {ImportProgress} from "../../../model/import/import-progress";
import {Subscription, timer} from "rxjs";
import {Import} from "../../../model/import/import";
import {UserService} from "../../../service/user-service/user.service";
import {Document} from "../../../model/irk/document";
import {MatDialog} from "@angular/material/dialog";
import {UpdateIndexNumberDialogComponent} from "../../dialog/update-index-number-dialog/update-index-number-dialog.component";

@Component({
  selector: 'app-import-view',
  templateUrl: './import-view.component.html',
  styleUrls: ['./import-view.component.sass']
})
export class ImportViewComponent implements OnInit, OnDestroy {

  importId: Number;
  import: Import;
  importProgress: ImportProgress;
  progressSubscription: Subscription;
  page: Page<Application>;
  dataSource = new MatTableDataSource<Application>();
  sortString: string = 'id';
  sortDirString: string = 'asc';
  displayedColumns: string[] = [
    'id',
    'irkId',
    'usosID',
    'names',
    'birthDateAndPlace',
    'pesel',
    'parentsNames',
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
  $importProgressObservable = timer(0, 1000).pipe(
    flatMap(() => this.importService.getImportProgress(this.importId)),
    tap(result => this.importProgress = result),
    flatMap(() => this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString)),
    tap(result => {
      if (this.importProgress.importStatus == "IMPORTED"
        || this.importProgress.importStatus == "COMPLETE"
        || this.importProgress.importStatus == "PENDING") {
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

  getImport(importId: Number) {
    return this.importService.getImport(importId).pipe(
      tap(importObject => this.import = importObject)
    )
  }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.importId = this.route.snapshot.params.id;
    if (!this.importId) {
      return
    }
    this.getImport(this.importId).subscribe()
    this.getPage(0, 15, this.sortString, this.sortDirString).subscribe(() => {
      this.progressSubscription = this.$importProgressObservable.subscribe()
    });
  }

  switchPage(pageEvent: PageEvent) {
    this.getPage(pageEvent.pageIndex, pageEvent.pageSize, this.sortString, this.sortDirString).subscribe();
  }

  sortEvent(sortEvent: Sort) {
    this.sortString = sortEvent.active;
    this.sortDirString = sortEvent.direction;
    this.getPage(this.page.number, this.page.size, this.sortString, this.sortDirString).subscribe();
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

  ngOnDestroy(): void {
    this.progressSubscription.unsubscribe()
  }
}
