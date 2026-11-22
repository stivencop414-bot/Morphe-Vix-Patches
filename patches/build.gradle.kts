group = "app.morphe"

patches {
    about {
        name = "Stiven Patches"
        description = "Community patches for Ditu, ViX, and TV Colombia Play compatible with Morphe."
        source = "https://github.com/stivencop414-bot/Morphe-Vix-Patches"
        author = "stivencop414-bot"
        contact = "https://github.com/stivencop414-bot/Morphe-Vix-Patches/issues"
        website = "https://github.com/stivencop414-bot/Morphe-Vix-Patches"
        license = "GNU General Public License v3.0, with additional GPL section 7 requirements"
    }
}

tasks.named<Jar>("jar") {
    from(rootProject.files("LICENSE", "NOTICE")) {
        into("META-INF")
    }
}

dependencies {
    compileOnly(libs.morphe.patcher)
    implementation(libs.gson)
    implementation(libs.guava)
}

tasks.register<JavaExec>("generatePatchesList") {
    description = "Generates patches-list.json from the built .mpp bundle."
    dependsOn(tasks.named("jar"))
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("app.morphe.util.PatchListGeneratorKt")
    args(project.version.toString())
}
