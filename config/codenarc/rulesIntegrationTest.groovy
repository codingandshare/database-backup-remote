ruleset {
  description 'CodeNarc RuleSet for Integration Test code'

  ruleset('rulesets/basic.xml') {
    exclude 'MethodReturnTypeRequired'
  }
  ruleset('rulesets/braces.xml')
  ruleset('rulesets/concurrency.xml')
  ruleset('rulesets/convention.xml') {
    exclude 'NoDef'
  }
  ruleset('rulesets/design.xml') {
    exclude 'Instanceof'
  }
  //ruleset('rulesets/dry.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/formatting.xml') {
    exclude 'SpaceAroundMapEntryColon' // This messes with IntelliJ's formatting of maps
    exclude 'SpaceAfterClosingBrace' // accept for `}]`
    exclude 'Indentation'
  }
  ruleset('rulesets/generic.xml') {
    exclude 'MethodReturnTypeRequired'
  }
  ruleset('rulesets/grails.xml')
  ruleset('rulesets/groovyism.xml')
  ruleset('rulesets/imports.xml') {
    MisorderedStaticImports {
      comesBefore = false
    }
    exclude 'NoWildcardImports'
  }
  ruleset('rulesets/jdbc.xml')
  ruleset('rulesets/junit.xml') {
    // JUnit properties mess with Spock way of things at times
    exclude 'JUnitAssertAlwaysFails'
    exclude 'JUnitAssertAlwaysSucceeds'
    exclude 'JUnitFailWithoutMessage'
    exclude 'JUnitLostTest'
    exclude 'JUnitPublicField'
    exclude 'JUnitPublicNonTestMethod'
    exclude 'JUnitPublicProperty'
    exclude 'JUnitSetUpCallsSuper'
    exclude 'JUnitStyleAssertions'
    exclude 'JUnitTearDownCallsSuper'
    exclude 'JUnitTestMethodWithoutAssert'
    exclude 'JUnitUnnecessarySetUp'
    exclude 'JUnitUnnecessaryTearDown'
    exclude 'JUnitUnnecessaryThrowsException'
  }
  ruleset('rulesets/logging.xml')
  ruleset('rulesets/naming.xml') {
    exclude 'MethodName' // for name of spock
  }
  ruleset('rulesets/security.xml')
  ruleset('rulesets/serialization.xml')
  ruleset('rulesets/size.xml') {
    exclude 'AbcComplexity' // DEPRECATED: Use the AbcMetric rule instead
    exclude 'CrapMetric' // This is broken
  }
  ruleset('rulesets/unnecessary.xml')
  ruleset('rulesets/unused.xml')
}
