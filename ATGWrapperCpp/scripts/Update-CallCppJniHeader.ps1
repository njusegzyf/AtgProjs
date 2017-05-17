$binPath = '/root/eclipseWorkspace/cn.nju.seg.atg/bin'
$moveToFolderPath = '/root/eclipseWorkspace/ATGWrapperCpp/src'
$targetClassName = 'cn.nju.seg.atg.callCPP.CallCPP'
$targetHeaderFileName = 'cn_nju_seg_atg_callCPP_CallCPP.h'

$currentLocation = Get-Location

# set location to bin folder
# Set-Location $binPath

# run javah
javah -classpath $binPath -jni $targetClassName

# move generated cpp header file
Move-Item -Path "./$targetHeaderFileName"  -Destination $moveToFolderPath

# restore location
# $currentLocation | Set-Location

