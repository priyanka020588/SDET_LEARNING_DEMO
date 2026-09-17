// Extent Spark reports use inline CSS/JS. Jenkins CSP blocks that by default,
// so the published HTML page would be blank. Safe for this local learning instance.
System.setProperty(
        "hudson.model.DirectoryBrowserSupport.CSP",
        "sandbox allow-scripts; default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;")
