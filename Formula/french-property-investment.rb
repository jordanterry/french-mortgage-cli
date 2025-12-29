class FrenchPropertyInvestment < Formula
  desc "Analyze French rental property investments with comprehensive cash flow projections"
  homepage "https://github.com/yourusername/french-property-investment"
  url "https://github.com/yourusername/french-property-investment/releases/download/v1.0.0/french-property-investment.jar"
  sha256 "PUT_SHA256_HERE"
  version "1.0.0"
  license "MIT"

  depends_on "openjdk@11"

  def install
    libexec.install "french-property-investment.jar"

    # Create wrapper script
    (bin/"french-property-investment").write <<~EOS
      #!/bin/bash
      exec "#{Formula["openjdk@11"].opt_bin}/java" -jar "#{libexec}/french-property-investment.jar" "$@"
    EOS
  end

  test do
    output = shell_output("#{bin}/french-property-investment --help 2>&1")
    assert_match "French Property Investment", output
  end
end
