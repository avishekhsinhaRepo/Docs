# We.Train site for Develop Websites and Components

## Modules

The main parts of the project are:

* core: Java bundle containing Java Code
  * com.adobe.training.core - These Java files are copied from the Extend and Customize AEM course code.
  * com.adobe.training.wetrain - These sling models are unique to Develop Websites and Components
* ui.apps: contains the /apps parts of the project, ie JS&CSS clientlibs, components, and templates
* ui.content: contains sample content using the components from the ui.apps

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
