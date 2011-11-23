<div id="page-wrapper">
  <!-- Content. -->
  <div id="content">

    <div id="content-inside" class="inside">
        <div id="main">
                                          
			<?php print render($title_prefix); ?>
            <?php if ($title): ?>
              <h1><?php print $title ?></h1>
            <?php endif; ?>
            <?php print render($title_suffix); ?>
           
            <?php print render($page['content']); ?>                       
            
        </div><!-- EOF: #main -->          
    </div><!-- EOF: #content-inside -->
       
  </div><!-- EOF: #content -->
</div><!-- EOF: #page-wrapper -->
