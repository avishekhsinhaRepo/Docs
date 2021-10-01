#!/bin/bash -x

mvn archetype:generate \
 -DarchetypeGroupId=com.adobe.granite.archetypes \
 -DarchetypeArtifactId=aem-project-archetype \
 -DarchetypeVersion=23 \
 -DgroupId=com.adobe.training \
 -DartifactId=wetrain \
 -Dversion=1.0-SNAPSHOT \
 -Dpackage=com.adobe.training \
 -DappId=training \
 -DappTitle=We.Train \
 -DaemVersion=cloud \
 -DsdkVersion=latest \
 -DlanguageCountry=en_us \
 -DincludeExamples=y \
 -DincludeErrorHandler=n \
 -DfrontendModule=none \
 -DsingleCountry=y \
 -DincludeDispatcherConfig=n