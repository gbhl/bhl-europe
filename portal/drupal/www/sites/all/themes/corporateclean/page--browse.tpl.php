<div id="page-wrapper">

<!-- Header. -->
<div id="header">
    <div id="header-inside">
       
        <div id="header-inside-left">
            
            <?php if ($logo): ?>
              <a href="<?php print check_url($front_page); ?>" title="<?php print t('Home'); ?>"  id="logo"><img src="/sites/default/files/logo-small.png" alt="<?php print t('Home'); ?>" /></a>
            <?php endif; ?>
     
            <!-- Skryty odkaz vedouci na napovedu ke klavesovym zkratkam -->
<!--            
            <p id="skip-link">
              <a href="http://www.ippi.cz/klavesove-zkratky/neni-mapa-stranek.html" accesskey="1" tabindex="1" class="skiplink">Klávesové zkratky na tomto webu - rozšírené</a>
              <a href="/" accesskey="2" tabindex="3" class="skiplink">Hlavní strana</a>
              <a href="/#obsah" class="skiplink" tabindex="2" accesskey="0">Na informační obsah stránky</a>
            </p>
-->
  
        </div>
           
        <div id="header-inside-right">
       		<?php print render($page['header']); ?>    

          <!-- Header Menu. -->
          <div id="header-menu">

            <div id="header-menu-inside">
            <?php 
          	$main_menu_tree = menu_tree(variable_get('menu_main_links_source', 'main-menu')); 
          	print drupal_render($main_menu_tree);
	          ?>
            </div><!-- EOF: #header-menu-inside -->

        </div><!-- EOF: #header-menu -->

        </div>
    
    </div><!-- EOF: #header-inside -->

</div><!-- EOF: #header -->

<!-- Content top -->
<?php if ($page['content_top']): ?>
  <div id="content_top">
	  <?php print render($page['content_top']); ?>
  </div><!-- EOF: #content_top -->
<?php endif; ?>        

<!-- Content. -->
<div id="content" class="<?php if($_SESSION['bhle_search']['browse']): ?>source-browse<?php else: ?>search-form-source<?php endif; ?>">

    <div id="content-inside" class="inside">
        <div id="main">
                       
            <?php if ($messages): ?>
            <div id="console" class="clearfix">
            <?php print $messages; ?>
            </div>
            <?php endif; ?>
     
            <?php if ($page['help']): ?>
            <div id="help">
            <?php print render($page['help']); ?>
            </div>
            <?php endif; ?>
                             
			      <?php print render($title_prefix); ?>
            <?php if ($title): ?>
              <h1><?php print $title ?></h1>
            <?php endif; ?>
            <?php print render($title_suffix); ?>
                    
            <div class="region region-content">
            <div id="block-system-main" class="block block-system ">
                <div class="content">
                <div class="content-inner">

                    <div id="content_preface">             
                      <?php print render($page['content_preface']); ?>
                    </div>    

                </div>
                </div>
            </div>
            </div>

            <?php if ($page['content_expanded']): ?><div id="content_expanded"><?php print render($page['content_expanded']); ?></div><?php endif; ?>           
                       
        </div><!-- EOF: #main -->          
    </div><!-- EOF: #content-inside -->
       
    <!-- Content bottom. -->
    <?php if ($page['content_bottom']): ?>
      <div id="content_bottom">
        <?php print render($page['content_bottom']); ?>
      </div><!-- EOF: #content_bottom -->
    <?php endif; ?>

</div><!-- EOF: #content -->

<!-- Footer -->    
<div id="footer">

    <div id="footer-inside">
    
        <div class="footer-area first">
        <?php print render($page['footer_first']); ?>
        </div><!-- EOF: .footer-area -->
        
        <div class="footer-area second">
        <?php print render($page['footer_second']); ?>
        </div><!-- EOF: .footer-area -->
        
        <div class="footer-area third">
        <?php print render($page['footer_third']); ?>
        </div><!-- EOF: .footer-area -->
       
    </div><!-- EOF: #footer-inside -->

</div><!-- EOF: #footer -->

<!-- Footer Bottom -->    
<div id="footer-bottom">
    <div id="footer-bottom-inside">                  
        <?php print render($page['footer_bottom']); ?>
    </div><!-- EOF: #footer-bottom-inside -->
</div><!-- EOF: #footer_bottom -->

</div><!-- EOF: #page-wrapper -->