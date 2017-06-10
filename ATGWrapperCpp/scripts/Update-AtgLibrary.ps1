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
  [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/stat', 'CallCPP_Stat.cpp', 'Stat'),
  [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/blindHashOpti', 'CallCPP.cpp', 'BlindHashOpti'),
  [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/dartAndEtc', 'CallCPP.cpp', 'DartAndEtc'),
  [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/tcas', 'CallCPP_Tcas.cpp', 'Tcas'),
  [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/raytrace', 'CallCPP_Raytrace1.cpp', 'Raytrace1')
  # [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/raytrace', 'CallCPP_Raytrace2.cpp', 'Raytrace2')
  # [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/raytrace', 'CallCPP_Raytrace3.cpp', 'Raytrace3')
  # [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/raytrace', 'CallCPP_Raytrace4.cpp', 'Raytrace4')
  # [LibraryInfo]::new('/root/eclipseWorkspace/ATGWrapperCpp/src/raytrace', 'CallCPP_Raytrace5.cpp', 'Raytrace5')
)
$libPrefix = 'CallCPP'
$jdkPath = '/usr/java/jdk1.8.0_121'

foreach ($library in $libraries){
  Set-Location -Path $library.libraryPath
  $libraryName = "lib$libPrefix$( $library.librarySuffix ).so"
  g++ "-I$jdkPath/include" "-I$jdkPath/include/linux" -fPIC -shared -o $libraryName $library.librarySource -std=c++11
  Copy-Item $libraryName -Destination $sysLibDirPath
}

# restore location
$currentLocation | Set-Location

