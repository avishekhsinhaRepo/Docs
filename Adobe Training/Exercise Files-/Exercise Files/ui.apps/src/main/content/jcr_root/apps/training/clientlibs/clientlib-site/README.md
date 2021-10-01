`Note, this code uses the WKND design which includes it's own version of the grid system`

## Use the AEM Grid System

If you want to include the standard OOTB grid system, simply add the category name: wcm.foundation.components.page.responsive


## Customize the AEM Grid System
If you want to customize the grid system, an example grid.less file is included in this clientlib. The grid.less file holds all the configurations to the grid system that AEM uses for responsive design. This file creates all the necessary css classes for Layout Mode.

The original grid.less file is located /libs/wcm/foundation/components/page/responsive/css/base.less.

If you would like to use and customize this grid.less file, you must:

  1. add the grid.less to the css.txt file
  2. You must also remove the category name, 'wcm.foundation.components.page.responsive' from your template Page Policy located in the Template Editor > Page Information > Page Policy

This will remove the default grid system from the template.
