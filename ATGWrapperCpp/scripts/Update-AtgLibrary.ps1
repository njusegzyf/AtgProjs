$currentLocation = Get-Location
$sysLibDirPath = '/usr/lib'

Class LibraryInfo{
    [String]$libraryPath;
    [String]$librarySource;
    [String]$librarySuffix;
    
    LibraryInfo([String]$libraryPathPar, [String]$librarySourcePar, [String]$librarySuffixPar) {
        $this.libraryPath = $libraryPathPar;
        $this.librarySource = $librarySourcePar;
        $this.librarySuffix = $librarySuffixPar;
    }
}


[LibraryInfo[]]$libraries = @(
    [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/coral', 'CallCPP.cpp', 'Coral'),
    # [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/stat', 'CallCPP_Stat.cpp', 'Stat'),
    [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/blindHashOpti', 'CallCPP.cpp', 'BlindHashOpti'),
    [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/dartAndEtc', 'CallCPP.cpp', 'DartAndEtc')
)
$libPrefix = 'CallCPP'
$jdkPath = '/usr/java/jdk1.8.0_121'

foreach ($library in $libraries){
  Set-Location -Path $library.libraryPath
  $libraryName = "lib$libPrefix$( $library.librarySuffix ).so"
  g++ "-I$jdkPath/include" "-I$jdkPath/include/linux" -fPIC -shared -o $libraryName $library.librarySource
  Copy-Item $libraryName -Destination $sysLibDirPath
}

# restore location
$currentLocation | Set-Location

