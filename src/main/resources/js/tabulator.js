AJS.toInit(function() {
    var baseUrl = AJS.$("meta[name='application-base-url']").attr("content");

    AJS.$(document).ready(function() {

        var selectedVersion = "";
        var location = window.location.pathname;

        AJS.$(".selectRelease").live("change", function() {
          var artifact = this.id;
          var selectArtifact = "#" + artifact;
          var divArtifact = "#ndiv-" + artifact;

          AJS.$(selectArtifact+".selectRelease option:selected").each(function() {
            selectedVersion = ""+ AJS.$(this).val() +"";
            AJS.$(selectArtifact).disable();
            AJS.$(divArtifact).fadeTo(500, 0.25);
            AJS.$(divArtifact+" [href]").removeAttr("href");

            AJS.$(divArtifact).load(location + ' '+divArtifact, 
              { releaseVer: selectedVersion }, function() {
                AJS.$(this).children().unwrap();
                AJS.$(divArtifact).fadeTo(500, 1.0);
                AJS.$(selectArtifact).enable();

              });

          });

        });

    });

});
