package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Requirements_1 : BuildType({
    id("Requirements")
    name = "Requirements"
    description = "Test the neccessary requirements are satisfied"

    vcs {
        root(_Self.vcsRoots.OpenEmbeddedCore, "+:. => src/oe-core")
        root(_Self.vcsRoots.Bitbake, "+:.=>src/oe-core/bitbake")
        root(_Self.vcsRoots.MetaAsteroid, "+:.=>src/meta-asteroid")

        cleanCheckout = true
    }

    steps {
        script {
            name = "Check minimal configuration"
            scriptContent = """
                mkdir -p build/conf
                echo 'DISTRO = "asteroid"
                PACKAGE_CLASSES = "package_ipk"' >> build/conf/local.conf
                echo 'BBPATH = "${'$'}{TOPDIR}"
                SRCDIR = "${'$'}{@os.path.abspath(os.path.join("${'$'}{TOPDIR}", "../src/"))}"
                
                BBLAYERS = "${'$'}{SRCDIR}/oe-core/meta"' > build/conf/bblayers.conf
                
                source ./src/oe-core/oe-init-build-env
                
                bitbake-selftest
            """.trimIndent()
        }
    }
})
